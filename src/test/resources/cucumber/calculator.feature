Feature: Addition
    In order to avoid silly mistakes
    As a math challenged person
    I want to be told the sum of two numbers

Scenario Outline: Add two numbers
    When I call the calculator add method with inputs <input_1> and <input_2>
    Then The result should be <output>

    Examples:
    | input_1 | input_2 | output |
    | 20      | 30      | 50     |
    | 2       | 5       | 7      |
    | 0       | 40      | 40     |
