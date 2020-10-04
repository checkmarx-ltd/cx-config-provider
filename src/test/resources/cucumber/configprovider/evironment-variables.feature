@Component
@Integration
Feature: Override existing config property with environment variables

    Scenario: basic exact match override
        This is the basic exact match override
        Given the following cofiguration:
        """
        test:
            simple_property: my simple property
            environment_variable: ${JAVA_HOME}

            included_simple_property: simple_property is ${test.simple_property} 
            included_environment_variable: environment_variable is ${test.environment_variable}
        """
        And environment variable named "JAVA_HOME" is set
        When resolving the configuration
        Then created resolved values are:
            | simple_property               | my simple property                              |
            | environment_variable          | ___path_to_JAVA_HOME___                         |
            | included_simple_property      | simple_property is my simple property           |
            | included_environment_variable | environment_variable is ___path_to_JAVA_HOME___ |

    @Skip
    Scenario: basic exact match override with system properties
        This is the basic exact match override
        Given the following cofiguration:
        """
        simple_property: my simple property
        system_property: ${system.prop}
        environment_variable: ${JAVA_HOME}

        included_simple_property: simple_property is ${simple_property} 
        included_system_property: system_property is ${system_property}
        included_environment_variable: environment_variable is ${environment_variable}
        """
        And  System property named "system.prop" is set to "my system property"
        And environment variable named "JAVA_HOME" is set to "test"
        When resolving the configuration
        Then created resolved values are:
            | variable                      | value                                           |
            | simple_property               | my simple property                              |
            | system_property               | my system property                              |
            | environment_variable          | ___path_to_JAVA_HOME___                         |
            | included_simple_property      | simple_property is my simple property           |
            | included_system_property      | system_property is my system property           |
            | included_environment_variable | environment_variable is ___path_to_JAVA_HOME___ |

