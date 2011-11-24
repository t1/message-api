package stockquote;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/sender")
public class StockQuoteSend extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Inject
    Event<StockQuote> send;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // we don't expect any GET parameters.
        response(response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request(request);
        response(response);
    }

    private void request(HttpServletRequest request) {
        String symbol = request.getParameter("symbol");
        BigDecimal price = new BigDecimal(request.getParameter("price"));
        send.fire(new StockQuote(symbol, price));
    }

    private void response(HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("  <head><title>Stock Quotes</title></head>");
        out.println("  <body>");
        out.println("    <h2>Please enter the stock quote</h2>");
        out.println("    <form method=\"post\" action=\"sender\"><p/>");
        out.println("      <input type=\"text\" name=\"symbol\" size=\"50\"/><br/>");
        out.println("      <input type=\"text\" name=\"price\" size=\"50\"/><br/>");
        out.println("      <input type=\"submit\" value=\"Send\" />");
        out.println("    </form>");
        out.println("  </body>");
        out.println("</html>");
    }
}
