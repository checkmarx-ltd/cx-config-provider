Feature: Configuration provider external apis tests

  @Skip
  Scenario: config provider: application.yml and github config-as-code
    Given application is started with AST parameters in application-test-api.yml
    And github repository contains config-as-code with AST preset
    When config provider is initialized by loading application-test-api.yml
    And github_token env property is initialized
    Then github repository loads its config-as-code
    And AST preset from application.yml is truncated by the preset from config-as-code


  Scenario: env variable truncates application.yml property
          - Application is started with GITHUB token application-test-api.yml
          - and GITHUB token are defined in environment
          - and config provider is initialized by loading application-test-api.yml
          - and env properties are loaded using config provider
    Given env variable truncates application.yml property
    Then GITHUB token from application-test-api.yml is truncated by those loaded from the env variables

  Scenario: application.yml properties truncate env variable 
      - Application is started with GITHUB token defined in environment
      - and GITHUB token application-test-api.yml
      - and config provider is initialized by loading environment
      - and application-test-api.yml is loaded using config provider
    Given application.yml properties truncate env variables
    Then GITHUB token from env variables is truncated by the one from application.yml

  