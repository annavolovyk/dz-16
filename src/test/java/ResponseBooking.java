import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ResponseBooking {
    private String status;
    private Booking booking;

}
