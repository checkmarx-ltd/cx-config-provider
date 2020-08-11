package com.cx;
import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.junit.Assert.assertEquals;

public class CalculatorTestSteps {

    int result;

    @When("^I call the calculator add method with inputs (\\d+) and (\\d+)$")
    public void i_call_the_calculator_add_method_with_inputs_and(int arg1, int arg2) {
        result =  Calculator.add(arg1,arg2);
    }

    @Then("^The result should be (\\d+)$")
    public void the_result_should_be(int arg1) {
        assertEquals(arg1, result);
    }


}
