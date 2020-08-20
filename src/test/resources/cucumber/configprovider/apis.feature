Feature: Configuration provider external apis tests

  
  Scenario: github config-as-code truncates application.yml 
           - application is started with GITHUB token and AST parameters in application-test-api.yml
           - github repository contains config-as-code with AST preset
           - and config provider is initialized by loading application-test-api.yml
           - and config provider is initialized with github repository
    Given github config-as-code truncates application.yml
    Then AST preset from application.yml is truncated by the preset from config-as-code


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

  