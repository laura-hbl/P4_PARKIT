package com.parkit.parkingsystem.unit;

import com.parkit.parkingsystem.util.InputReaderUtil;
import com.parkit.parkingsystem.util.ScannerWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InputReaderUtilTest {

    private InputReaderUtil inputReaderUtil;

    @Mock
    ScannerWrapper scanner;


    @BeforeEach
    public void setUp() {
        inputReaderUtil = new InputReaderUtil(scanner);
    }

    @Test
    @Tag("ReadSelection")
    @DisplayName("Given valid number input, when readSelection, then reads input")
    public void givenNumericalInput_whenReadSelection_thenSelectionEqualsInput() {
        when(scanner.nextLine()).thenReturn("1");

        int selection = inputReaderUtil.readSelection();

        assertThat(selection).isEqualTo(1);
    }

    @Test
    @Tag("ReadSelection")
    @DisplayName("Given negative number input, when readSelection, then returns -1")
    public void givenNegativeNumericalInput_whenReadSelection_thenSelectionDoesNotEqualInput() {
        when(scanner.nextLine()).thenReturn("-2");

        int selection = inputReaderUtil.readSelection();

        assertThat(selection).isEqualTo(-1);
    }

    @Test
    @Tag("ReadSelection")
    @DisplayName("Given invalid number input, when readSelection, then returns -1")
    public void givenInvalidNumericalInput_whenReadSelection_thenSelectionDoesNotEqualInput() {
        when(scanner.nextLine()).thenReturn("4");

        int selection = inputReaderUtil.readSelection();

        assertThat(selection).isEqualTo(-1);
    }

    @Test
    @Tag("ReadSelection")
    @DisplayName("Given one letter input, when readSelection, then returns -1")
    public void givenStringInput_whenReadSelection_thenSelectionDoesNotEqualInput() {
        when(scanner.nextLine()).thenReturn("a");

        int selection = inputReaderUtil.readSelection();

        assertThat(selection).isEqualTo(-1);
    }

    @Test
    @Tag("ReadVehicleRegistrationNumber")
    @DisplayName("Given valid registration number input, when readVehicleRegistrationNumber, then reads input")
    public void givenRegistrationNumberInput_whenReadVehicleRegistrationNumber_thenReadRegistrationNumberEqualsInput() {
        when(scanner.nextLine()).thenReturn("AB125XY");

        String regNumber = inputReaderUtil.readVehicleRegistrationNumber();

        assertThat(regNumber).isEqualTo("AB125XY");
    }

    @Test
    @Tag("ReadVehicleRegistrationNumber")
    @DisplayName("Given invalid registration number input, when readVehicleRegistrationNumber, then throws IllegalArgumentException")
    public void givenInvalidRegistrationNumberInput_whenReadVehicleRegistrationNumber_thenReadRegistrationNumberEqualsInput() {
        when(scanner.nextLine()).thenReturn("AB125XYH34");

        assertThrows(IllegalArgumentException.class, () -> inputReaderUtil.readVehicleRegistrationNumber());
    }

    @Test
    @Tag("ReadVehicleRegistrationNumber")
    @DisplayName("Given empty input, when readVehicleRegistrationNumber, then throws IllegalArgumentException")
    public void givenEmptyRegistrationNumberInput_whenReadVehicleRegistrationNumber_thenIllegalArgumentExceptionThrown() {
        when(scanner.nextLine()).thenReturn("");

        assertThrows(IllegalArgumentException.class, () -> inputReaderUtil.readVehicleRegistrationNumber());
    }

    @Test
    @Tag("ReadVehicleRegistrationNumber")
    @DisplayName("Given null input, when readVehicleRegistrationNumber, then throws IllegalArgumentException")
    public void givenNullRegistrationNumberInput_whenReadVehicleRegistrationNumber_thenIllegalArgumentExceptionThrown() {
        when(scanner.nextLine()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> inputReaderUtil.readVehicleRegistrationNumber());
    }
}