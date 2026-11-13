package com.releasesignal.transfer.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class TransferApiIT {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "";
    }

    @Test
    @DisplayName("Given the app is up When GET /actuator/health Then status is UP")
    void healthIsUp() {
        given()
                .when()
                .get("/actuator/health")
                .then()
                .statusCode(200)
                .body("status", equalTo("UP"));
    }

    @Test
    @DisplayName("Given sufficient balance When POST transfer Then balances move by amount and 201 with transfer id")
    void successfulTransferUpdatesBalances() {
        BigDecimal aliceBefore = balance("alice");
        BigDecimal bobBefore = balance("bob");
        var amount = new BigDecimal("10.00");

        String key = newIdempotencyKey();
        String transferId = given()
                .contentType(ContentType.JSON)
                .header("Idempotency-Key", key)
                .body(Map.of("fromAccountId", "alice", "toAccountId", "bob", "amount", amount))
                .when()
                .post("/api/transfers")
                .then()
                .statusCode(201)
                .body("status", equalTo("COMPLETED"))
                .extract()
                .path("transferId");

        assertThat(transferId).isNotBlank();
        assertThat(balance("alice")).isEqualByComparingTo(aliceBefore.subtract(amount));
        assertThat(balance("bob")).isEqualByComparingTo(bobBefore.add(amount));
    }

    @Test
    @DisplayName("Given insufficient balance When POST transfer Then 422 and balances unchanged")
    void insufficientBalanceDoesNotMutate() {
        BigDecimal aliceBefore = balance("alice");
        BigDecimal bobBefore = balance("bob");
        var tooMuch = new BigDecimal("1000.00");

        given().contentType(ContentType.JSON)
                .header("Idempotency-Key", newIdempotencyKey())
                .body(Map.of("fromAccountId", "alice", "toAccountId", "bob", "amount", tooMuch))
                .when()
                .post("/api/transfers")
                .then()
                .statusCode(422)
                .body("code", equalTo("INSUFFICIENT_BALANCE"));

        assertThat(balance("alice")).isEqualByComparingTo(aliceBefore);
        assertThat(balance("bob")).isEqualByComparingTo(bobBefore);
    }

    @Test
    @DisplayName("Given invalid amount When POST transfer Then 400 and balances unchanged")
    void validationErrorForNonPositiveAmount() {
        BigDecimal aliceBefore = balance("alice");
        BigDecimal bobBefore = balance("bob");

        given().contentType(ContentType.JSON)
                .header("Idempotency-Key", newIdempotencyKey())
                .body(Map.of("fromAccountId", "alice", "toAccountId", "bob", "amount", 0))
                .when()
                .post("/api/transfers")
                .then()
                .statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"));

        assertThat(balance("alice")).isEqualByComparingTo(aliceBefore);
        assertThat(balance("bob")).isEqualByComparingTo(bobBefore);
    }

    @Test
    @DisplayName("Given identical retry When POST with same key and body Then 200 same transfer id and no extra debit")
    void identicalReplayReturnsOriginalResult() {
        var amount = new BigDecimal("5.00");
        String key = newIdempotencyKey();

        BigDecimal aliceBefore = balance("alice");
        BigDecimal bobBefore = balance("bob");

        String firstId = given()
                .contentType(ContentType.JSON)
                .header("Idempotency-Key", key)
                .body(Map.of("fromAccountId", "alice", "toAccountId", "bob", "amount", amount))
                .when()
                .post("/api/transfers")
                .then()
                .statusCode(201)
                .extract()
                .path("transferId");

        String secondId = given()
                .contentType(ContentType.JSON)
                .header("Idempotency-Key", key)
                .body(Map.of("fromAccountId", "alice", "toAccountId", "bob", "amount", amount))
                .when()
                .post("/api/transfers")
                .then()
                .statusCode(200)
                .body("transferId", equalTo(firstId))
                .extract()
                .path("transferId");

        assertThat(secondId).isEqualTo(firstId);
        assertThat(balance("alice")).isEqualByComparingTo(aliceBefore.subtract(amount));
        assertThat(balance("bob")).isEqualByComparingTo(bobBefore.add(amount));
    }

    @Test
    @DisplayName("Given same key and different amount When POST Then 409 and balances unchanged from first transfer")
    void sameKeyDifferentBodyRejected() {
        var firstAmount = new BigDecimal("7.00");
        var secondAmount = new BigDecimal("8.00");
        String key = newIdempotencyKey();

        BigDecimal aliceBefore = balance("alice");
        BigDecimal bobBefore = balance("bob");

        given().contentType(ContentType.JSON)
                .header("Idempotency-Key", key)
                .body(Map.of("fromAccountId", "alice", "toAccountId", "bob", "amount", firstAmount))
                .when()
                .post("/api/transfers")
                .then()
                .statusCode(201);

        given().contentType(ContentType.JSON)
                .header("Idempotency-Key", key)
                .body(Map.of("fromAccountId", "alice", "toAccountId", "bob", "amount", secondAmount))
                .when()
                .post("/api/transfers")
                .then()
                .statusCode(409)
                .body("code", equalTo("IDEMPOTENCY_CONFLICT"));

        assertThat(balance("alice")).isEqualByComparingTo(aliceBefore.subtract(firstAmount));
        assertThat(balance("bob")).isEqualByComparingTo(bobBefore.add(firstAmount));
    }

    private static BigDecimal balance(String accountId) {
        String raw = given().when()
                .get("/api/accounts/" + accountId + "/balance")
                .then()
                .statusCode(200)
                .extract()
                .path("balance")
                .toString();
        return new BigDecimal(raw);
    }

    private static String newIdempotencyKey() {
        return UUID.randomUUID().toString();
    }
}
