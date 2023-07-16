import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.RestAssured;
import org.json.JSONObject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;

public class RestfulBookingTests {

    private static final String AUTH_ENDPOINT = "/auth";
    private static final String BOOKING_ENDPOINT = "/booking";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "password123";

    private String authToken;

    Date date = new Date();
    SimpleDateFormat dmyFormat = new SimpleDateFormat("yyyy-MM-dd");
    String formattedDate = dmyFormat.format(date);


    @BeforeMethod
    public void setUp() {
        RestAssured.baseURI = "http://restful-booker.herokuapp.com";
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
    }

    @Test
    public void authorizeTest() {
        JSONObject requestBody = new JSONObject();
        requestBody.put("username", USERNAME);
        requestBody.put("password", PASSWORD);

        Response authorize = RestAssured.given()
                .basePath(AUTH_ENDPOINT)
                .body(requestBody.toString())
                .post();
        authorize.then().statusCode(200);
        authToken = authorize.getBody().jsonPath().getString("token");
    }

  @Test
    public void getBookingIdsTest() {
        Response response = RestAssured.given().log().all().get("/booking");
        response.then().statusCode(200);
        response.prettyPrint();
    }

    @Test
    public void createNewBooking() {
        Booking body = Booking.builder()
                .firstname("Jim")
                .lastname("Brown")
                .totalprice(111)
                .depositpaid(true)
                .bookingdates(BookingDates.builder()
                        .checkin(formattedDate)
                        .checkout(formattedDate)
                        .build())
                .additionalneeds("Breakfast")
                .build();

        Response booking = RestAssured.given()
                .basePath("/booking")
                .header("Content-Type", ContentType.JSON.toString())
                .body(body)
                .post();
        booking.then().statusCode(200);
        booking.prettyPrint();
    }

    @Test
    public void putBookingTest() {
        int bookingId = 17;

        JSONObject body = new JSONObject();
        body.put("firstname", "James");
        body.put("additionalneeds", "Breakfast");

        Response put = RestAssured.given()
                .basePath(BOOKING_ENDPOINT + "/" + bookingId)
                .header("Accept", ContentType.JSON.toString())
                .header("Cookie", "token=" + authToken)
                .body(body.toString())
                .put();
        put.then().statusCode(200);
        put.prettyPrint();
    }

    @Test
    public void updateBookingTest(){

        JSONObject body = new JSONObject();
        body.put("totalprice", 100);

        Response updatedBooking = RestAssured.given()
                .basePath(BOOKING_ENDPOINT)
                .header("Accept", ContentType.JSON.toString())
                .header("Cookie", "token=" + authToken)
                .body(body.toString())
                .patch(BOOKING_ENDPOINT + "/{id}", 5179);
        updatedBooking.then().statusCode(200);
        updatedBooking.prettyPrint();
    }
    @Test
    public void deleteBookingTest() {
        int bookingId = 17;

        Response delete = RestAssured.given()
                .basePath(BOOKING_ENDPOINT + "/" + bookingId)
                .header("Cookie", "token=" + authToken)
                .delete();
        delete.then().statusCode(201);
        delete.prettyPrint();
    }
}