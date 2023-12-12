package ru.job4j.service;

import org.apache.commons.dbcp2.BasicDataSource;
import ru.job4j.model.Ticket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class TicketService implements Service<Ticket> {
    private final BasicDataSource pool = new BasicDataSource();

    private TicketService() {
        Properties cfg = new Properties();
        try (BufferedReader io = new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(TicketService.class.getClassLoader()
                                .getResourceAsStream("db.properties"))
                )
        )) {
            cfg.load(io);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);
    }

    private static final class Lazy {
        private static final TicketService INST = new TicketService();
    }

    public static TicketService instOf() {
        return TicketService.Lazy.INST;
    }

    @Override
    public Collection<Ticket> findAll() {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection cn = pool.getConnection()) {
            PreparedStatement ps = cn.prepareStatement("SELECT * FROM tickets");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tickets.add(new Ticket(rs.getInt("id"), rs.getInt("seat_id"),
                            rs.getInt("account_id"), rs.getInt("session_id")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tickets.sort(Comparator.comparingInt(Ticket::getId));
        return tickets;
    }

    @Override
    public Ticket findById(int id) {
        try (Connection cn = pool.getConnection()) {
            PreparedStatement ps = cn.prepareStatement("SELECT * FROM tickets WHERE id=?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Ticket(rs.getInt("id"), rs.getInt("seat_id"),
                            rs.getInt("account_id"), rs.getInt("session_id"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Ticket createTicket(Ticket ticket) {
        try (Connection cn = pool.getConnection()) {
            PreparedStatement ps = cn.prepareStatement(
                    "INSERT INTO tickets (seat_id, account_id, session_id) VALUES (?,?,?)",
                    PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, ticket.getSeatId());
            ps.setInt(2, ticket.getAccountId());
            ps.setInt(3, ticket.getSessionId());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    ticket.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ticket;
    }
}
