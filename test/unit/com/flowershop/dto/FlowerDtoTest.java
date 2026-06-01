package com.flowershop.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FlowerDtoTest {

    @Test
    void testConstructorAndGetters() {
        FlowerDto dto = new FlowerDto("Rose", 10.5, "Red");
        assertEquals("Rose", dto.getName());
        assertEquals(10.5, dto.getPrice());
        assertEquals("Red", dto.getColor());
    }

    @Test
    void testSetters() {
        FlowerDto dto = new FlowerDto();
        dto.setName("Tulip");
        dto.setPrice(5.0);
        dto.setColor("Yellow");

        assertEquals("Tulip", dto.getName());
        assertEquals(5.0, dto.getPrice());
        assertEquals("Yellow", dto.getColor());
    }
}
