import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.uc.pairtest.TicketTypeRequestUtil;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TicketTypeRequestUtilTests {

    private TicketTypeRequestUtil ticketTypeRequestUtil;
    private TicketTypeRequest adult;
    private TicketTypeRequest child;
    private TicketTypeRequest infant;

    @BeforeEach
    public void setUp() {
        adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 3);
        child = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2);
        infant = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);
        ticketTypeRequestUtil = new TicketTypeRequestUtil();
    }

    @Test
    public void test_getPriceSingleTicketType() {
        assertEquals(75, ticketTypeRequestUtil.getPrice(adult));
    }

    @Test
    public void test_getPriceMultipleTicketTypes() {
        assertEquals(105, ticketTypeRequestUtil.getPrice(adult, child));
    }

    @Test
    public void test_assignsAdultAndChildSeats() {
        assertEquals(5, ticketTypeRequestUtil.getTotalSeats(adult, child));
    }

    @Test
    public void test_doesNotAssignSeatToInfant() {
        assertEquals(5, ticketTypeRequestUtil.getTotalSeats(adult, child, infant));
    }
}
