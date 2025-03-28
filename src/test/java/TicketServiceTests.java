import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.TicketTypeRequestUtil;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTests {

    @Mock
    private TicketPaymentService paymentService;

    @Mock
    private SeatReservationService seatReservationService;

    @Mock
    private TicketTypeRequestUtil ticketTypeRequestUtil;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private TicketTypeRequest adult;
    private TicketTypeRequest child;

    @BeforeEach
    public void setUp() {
        adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 3);
        child = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2);
    }

    @Test
    public void test_invalidAccountNumberThrowsException() {
        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(0L, adult));
    }

    @Test
    public void test_negativeQuantityThrowsException() {
        TicketTypeRequest invalidRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, -1);

        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(3L, invalidRequest));
    }

    @Test
    public void test_childWithoutAdultThrowsException() {
        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(1L, child));
    }

    @Test
    public void test_over25TicketsOneTicketTypeThrowsException() {
        TicketTypeRequest invalidRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 26);
        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(1L, invalidRequest));
    }

    @Test
    public void test_over25TicketsTwoTicketTypesThrowsException() {
        TicketTypeRequest childTickets = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 13);
        TicketTypeRequest adultTickets = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 13);
        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(1L, childTickets, adultTickets));
    }

    @Test
    public void test_sendsPaymentRequest() {
        Mockito.when(ticketTypeRequestUtil.getPrice(adult)).thenReturn(75);
        ticketService.purchaseTickets(1L, adult);
        verify(paymentService).makePayment(1L, 75);
    }

    @Test
    public void test_sendsSeatReservationRequest() {
        Mockito.when(ticketTypeRequestUtil.getTotalSeats(adult)).thenReturn(3);
        ticketService.purchaseTickets(1L, adult);
        verify(seatReservationService).reserveSeat(1L, 3);
    }
}
