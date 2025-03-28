package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

import java.util.Arrays;

public class TicketTypeRequestUtil {

    public int getTotalSeats(TicketTypeRequest... ticketTypeRequests) {
        return Arrays.stream(ticketTypeRequests)
                .filter(req -> req.getTicketType() != TicketTypeRequest.Type.INFANT)
                .mapToInt(TicketTypeRequest::getNoOfTickets)
                .sum();
    }

    public int getPrice(TicketTypeRequest... ticketTypeRequests) {
        return Arrays.stream(ticketTypeRequests).mapToInt(req ->
                req.getTicketType().getPrice() * req.getNoOfTickets()).sum();
    }
}
