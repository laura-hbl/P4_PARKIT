package com.parkit.parkingsystem.unit;

import com.parkit.parkingsystem.util.InputReaderUtil;
import com.parkit.parkingsystem.util.ScannerWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InputReaderUtilTest {

    private static final String REG_NUMBER = "ABCDEF";

    private InputReaderUtil inputReaderUtil;

    @Mock
    ScannerWrapper scanner;


    @BeforeEach
    public void setUp() {
        inputReaderUtil = new InputReaderUtil(scanner);
    }

    @Test
    public void givenRegistrationNumberInput_whenReadVehicleRegistrationNumber_thenReadRegistrationNumberEqualsInput() {
        when(scanner.nextLine()).thenReturn(REG_NUMBER);
        String regNumber = inputReaderUtil.readVehicleRegistrationNumber();
        assertThat(regNumber).isEqualTo(REG_NUMBER);
    }

    @Test
    public void givenInvalidRegistrationNumberInput_whenReadVehicleRegistrationNumber_thenIllegalArgumentExceptionThrown() {
        when(scanner.nextLine()).thenReturn("");
        assertThrows(IllegalArgumentException.class, () -> inputReaderUtil.readVehicleRegistrationNumber());
    }

    @Test
    public void givenNullRegistrationNumberInput_whenReadVehicleRegistrationNumber_thenIllegalArgumentExceptionThrown() {
        when(scanner.nextLine()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> inputReaderUtil.readVehicleRegistrationNumber());
    }

    @Test
    public void givenNumericalInput_whenReadSelection_thenSelectionEqualsInput() {
        when(scanner.nextLine()).thenReturn("1");
        int selection = inputReaderUtil.readSelection();
        assertThat(selection).isEqualTo(1);
    }

    @Test
    public void givenStringInput_whenReadSelection_thenSelectionDoesNotEqualInput() {
        when(scanner.nextLine()).thenReturn("a");
        int selection = inputReaderUtil.readSelection();
        assertThat(selection).isEqualTo(-1);
    }
}