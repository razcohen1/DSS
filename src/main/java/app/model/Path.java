package app.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class Path {
    List<Street> streets;
    double timePassed;
    double score;

    public void addStreet(Street street) {
        streets.add(street);
    }

    public void removeLastStreet() {
        streets.remove(streets.size() - 1);
    }

    public void increaseScoreBy(double value) {
        this.score += value;
    }

    public void decreaseScoreBy(double value) {
        this.score -= value;
    }

    public void increaseTimePassedBy(double value) {
        this.timePassed += value;
    }

    public void decreaseTimePassedBy(double value) {
        this.timePassed -= value;
    }

    public Street getFirstStreet(){
        return streets.get(0);
    }

    public Street getLastAddedStreet(){
        return streets.get(streets.size()-1);
    }

    public boolean isEmpty() {
        return streets.isEmpty();
    }
}
