package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.Arrays;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */
    private final TicketPaymentService paymentService;
    private final SeatReservationService seatReservationService;

    private final TicketTypeRequestUtil ticketTypeRequestUtil;

    public TicketServiceImpl(TicketPaymentService paymentService, SeatReservationService seatReservationService, TicketTypeRequestUtil ticketTypeRequestUtil) {
        this.paymentService = paymentService;
        this.seatReservationService = seatReservationService;
        this.ticketTypeRequestUtil = ticketTypeRequestUtil;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        boolean isInvalidAccountId = accountId <= 0;
        boolean hasNegativeTickets = Arrays.stream(ticketTypeRequests)
                .anyMatch(req -> req.getNoOfTickets() < 0);
        boolean noAdultTickets = Arrays.stream(ticketTypeRequests)
                .noneMatch(req -> req.getTicketType() == TicketTypeRequest.Type.ADULT);
        boolean over25Tickets = Arrays.stream(ticketTypeRequests).filter(req -> req.getTicketType() != TicketTypeRequest.Type.INFANT)
                .mapToInt(TicketTypeRequest::getNoOfTickets).sum() > 25;

        if (isInvalidAccountId || hasNegativeTickets || noAdultTickets || over25Tickets) {
            throw new InvalidPurchaseException();
        }
        paymentService.makePayment(accountId, ticketTypeRequestUtil.getPrice(ticketTypeRequests));
        seatReservationService.reserveSeat(accountId, ticketTypeRequestUtil.getTotalSeats(ticketTypeRequests));
    }

}
