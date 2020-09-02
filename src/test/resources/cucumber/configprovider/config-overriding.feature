@Skip
Feature: As a client using ConfigProvider, I want my configuration to be loaded from several known sources
    using a fixed override order

    Background: Default configuration contains all necessary info for accessing the target GitHub repository: URL, token etc.

    Override order (next overrides previous):
    1. Default configuration (local YAML file)
    2. Config-as-code YAML from a remote repository
    3. Environment variables


    Scenario Outline: overriding default configuration with config-as-code YAML file
    Check that (2) overrides (1)
        Given default configuration contains "<property>" set to <default> value
        And config-as-code YAML file in the GitHub repository contains "<property>" with the <remote YAML> value
        When initializing ConfigProvider with the default configuration
        And using ConfigProvider to load config-as-code
        Then the resulting config will have the "<property>" set to the <resulting> value
        Examples:
            | property                | default               | remote YAML          | resulting            |
            | cx-flow.thresholds.high | 3                     | 5                    | 5                    |
            | gitlab.url              | "https://example.com" | "https://gitlab.com" | "https://gitlab.com" |
            | log-level               | "info"                | "debug"              | "debug"              |
            | log-level               | <null>                | "debug"              | "debug"              |
            | sca.active              | <missing>             | true                 | true                 |
            | log-level               | "info"                | <missing>            | "info"               |
            | log-level               | "info"                | <null>               | "info"               |


    Scenario Outline: overriding configuration from config-as-code YAML file with environment variables
    Check that (3) overrides (2). Also verify the mapping from YAML properties to env variable names.
        Given default configuration doesn't contain properties defined in the examples
        And config-as-code YAML file in the GitHub repository contains "<YAML property>" with the "<YAML value>" value
        And the "<env variable name>" environment variable has the "<env variable value>" value
        When initializing ConfigProvider with the default configuration
        And using ConfigProvider to load config-as-code
        Then the resulting config will have the "<YAML property>" set to the <resulting> value
        Examples:
            | YAML property           | YAML value           | env variable name       | env variable value    | resulting             |
            | cx-flow.thresholds.high | 5                    | CX_FLOW_THRESHOLDS_HIGH | 12                    | 12                    |
            | gitlab.url              | "https://gitlab.com" | GITLAB_URL              | "https://example.com" | "https://example.com" |
            | log-level               | "debug"              | LOG_LEVEL               | "warning"             | "warning"             |
            | sca.active              | true                 | SCA_ACTIVE              | <missing>             | true                  |
            | log-level               | <missing>            | LOG_LEVEL               | "info"                | "info"                |
            | log-level               | <null>               | LOG_LEVEL               | "info"                | "info"                |
