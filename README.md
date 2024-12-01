# Eternity Calculator Application

Eternity is an advanced scientific calculator application designed to provide a seamless user experience for both standard and advanced mathematical computations. It offers functionalities such as power computations, logarithms, trigonometric functions, standard deviation, and more, with robust input validation and intuitive graphical outputs.

## Features

### Core Functionalities:
- **Power Function (`x^y`)**: Computes the result of raising `x` to the power `y`, with graphical visualization.
- **Logarithmic Functions**: Computes logarithms with customizable bases, ensuring error handling for invalid inputs.
- **Gamma Function (`Γ(x)`)**: Calculates gamma values, with validation for undefined cases (e.g., negative integers).
- **Standard Deviation Wizard**: Offers two modes for calculating standard deviation:
  - Manual data input.
  - Importing data directly from Excel files.
- **Mean Absolute Deviation (MAD)**: Calculates MAD for datasets with robust input validation.
- **Predefined Constants**: Provides quick access to constants like `π` and `e`.
- **Trigonometric and Hyperbolic Functions**: Includes advanced functions such as `arccos(x)` and `sinh(x)`.

### User Interface Highlights:
- Clean, intuitive design with color-coded buttons:
  - **Gray Buttons**: Numeric inputs.
  - **Orange Buttons**: Arithmetic operations.
  - **Blue Buttons**: Advanced functionalities.
  - **Green Buttons**: Predefined constants.
- Error handling with descriptive messages for invalid inputs.
- Graphical visualization of computations to aid user understanding.

### Robust Error Handling:
- Prevents invalid inputs like multiple decimal points.
- Handles undefined mathematical scenarios gracefully (e.g., log base 0 or negative inputs to gamma).

## Getting Started

### Prerequisites
- Java Development Kit (JDK) 8 or later.
- JavaFX library for graphical user interface.
- An IDE such as IntelliJ IDEA, Eclipse, or Visual Studio Code.

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/eternity-calculator.git
   cd eternity-calculator
