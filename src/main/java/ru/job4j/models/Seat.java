package ru.job4j.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Seat {
    private int id;
    private int idUser;
    private final int row;
    private final int seat;
    private boolean isBought;

    public Seat(int id, int row, int seat) {
        this.id = id;
        this.row = row;
        this.seat = seat;
    }
}
