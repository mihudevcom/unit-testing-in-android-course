package com.techyourchance.unittestingfundamentals.exercise3;

import com.techyourchance.unittestingfundamentals.example3.Interval;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IntervalsAdjacencyDetectorTest {

    IntervalsAdjacencyDetector SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new IntervalsAdjacencyDetector();
    }

    //interval 1 is before interval 2
    @Test
    public void isAdjacent_interval1BeforeInterval2_falseReturned() {
        Interval interval1 = new Interval(0, 5);
        Interval interval2 = new Interval(7, 10);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    //interval 1 is adjacent to interval 2 start
    @Test
    public void isAdjacent_interval1AdjacentStartOfInterval2_trueReturned() {
        Interval interval1 = new Interval(0, 5);
        Interval interval2 = new Interval(5, 10);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(true));
    }
    //interval 1 is inside interval 2

    @Test
    public void isAdjacent_interval1InsideInterval2_falseReturned() {
        Interval interval1 = new Interval(0, 5);
        Interval interval2 = new Interval(-3, 10);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    //interval 1 is same as interval 2
    @Test
    public void isAdjacent_interval1SameAsInterval2_falseReturned() {
        Interval interval1 = new Interval(0, 5);
        Interval interval2 = new Interval(0, 5);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    //interval 1 starts inside interval 2 but ends after it
    @Test
    public void isAdjacent_interval1StartInsideInterval2EndAfter_falseReturned() {
        Interval interval1 = new Interval(3, 15);
        Interval interval2 = new Interval(0, 10);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    //interval 1 is adjacent to interval 2 on the right
    @Test
    public void isAdjacent_interval1AdjacentInterval2End_trueReturned() {
        Interval interval1 = new Interval(10, 20);
        Interval interval2 = new Interval(7, 10);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(true));
    }

    //interval 1 starts inside interval 2 but is adjacent to right of interval 2
    @Test
    public void isAdjacent_interval1StartInInterval2AdjacentInterval2End_falseReturned() {
        Interval interval1 = new Interval(6, 10);
        Interval interval2 = new Interval(0, 10);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    //interval 1 is after interval 2
    @Test
    public void isAdjacent_interval1AfterInterval2_falseReturned() {
        Interval interval1 = new Interval(16, 20);
        Interval interval2 = new Interval(0, 10);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }
}