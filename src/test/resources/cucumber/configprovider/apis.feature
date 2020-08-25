Feature: Configuration provider external apis tests
  
  Scenario Outline: env variable truncates application.yml property
          - Application is started with GITHUB token application-test-api.yml
          - and GITHUB token is defined in environment
          - and config provider is initialized by loading application-test-api.yml
          - and env properties are loaded using config provider
    Given env variable truncates application.yml property
    Then GITHUB token from application-test-api.yml is truncated by "<github_token>" loaded from the env variables
    Examples:
      | github_token       |
      | envPropGitHubToken |


  Scenario Outline: initial resource is a results of several resources: application.yml, env variables and application-secrets.yml
    Given application.yml, env variables and application-secrets.yml are loaded into initial resource using MultipleResourcesImpl
    Then GITHUB token from application-test-api.yml is truncated by "<github_token>" loaded from the env variables
    And AST token "<ast_token>" and AST preset "<ast_preset>" will be taken from application-secrets.yml
    And the order of truncation will be based on the order of the files added to the MultipleResourcesImpl

    Examples:
      | github_token       | ast_token| ast_preset|
      | envPropGitHubToken | astTokenFromSecrets| presetFromAppSecretsYml|
  
    
  Scenario Outline: application.yml properties over env variable 
      - Application is started with GITHUB token defined in environment
      - and GITHUB token application-test-api.yml
      - and config provider is initialized by loading environment
      - and application-test-api.yml is loaded using config provider
    Given application.yml properties truncate env variables
    Then GITHUB token from env variables is truncated by the "<github_token>" from application.yml
    Examples:
      | github_token       |
      | githubTokenFromApp |

    
  Scenario Outline: github config-as-code(GITHUB) over env variables over application.yml
  - application is started with GITHUB token and AST parameters in application-test-api.yml
  - and GITHUB token is defined in env variables
  - github repository contains config-as-code with AST preset
  - and config provider is initialized by loading application-test-api.yml
  - and config provider is initialized by loading env variables 
  - and config provider is initialized with github repository
    Given github config-as-code(GITHUB) over env variables over application.yml in branch "<branch>"
    Then AST preset from application.yml is truncated by the preset from config-as-code "<preset_result>"
    Examples:
      | branch | preset_result          | 
      | test1  | presetFromConfigAsCode | 


  Scenario Outline: github config-as-code.yml over config-as-code(GITHUB) over env variables over application.yml
  - application is started with GITHUB token and AST parameters in application-test-api.yml
  - and GITHUB token is defined in environment
  - github repository contains config-as-code with AST preset
  - and github repository contains also config-as-code.yml in the .checkmarx folder
  - and config provider is initialized by loading application-test-api.yml and env variables
  - and config provider is initialized with github repository
    Given config-as-code.yml over config-as-code(GITHUB) over env variables over application.yml in github branch "<branch>"
    Then AST preset from application.yml and preset from config-as-code is truncated by the preset from config-as-code.yml "<preset_result>"
    Examples:
      | branch | preset_result       |
      | test2  | presetFromGithubYml | 
    
  Scenario Outline: b.yml(GITHUB) over a.yml(GITHUB) over config-as-code(GITHUB) over env variables over application.yml
  - application is started with GITHUB token and AST parameters in application-test-api.yml
  - and GITHUB token is defined in environment
  - and config-as-code file name is defined in application.yml as cx.configuration
  - github repository contains cx.configuration with AST preset
  - github repository contains .checkmarx folder with a.yml and b.yml
  - and they both contain contains AST preset field and additional unique fields
  - and config provider is initialized by loading application-test-api.yml and env variables
  - and config provider is initialized with github repository
    Given in github branch "<branch>": b.yml(GITHUB) over a.yml(GITHUB) over config-as-code(GITHUB) over env variables over application.yml
    Then unique elements form all configuration file will exist in in the final configuration
    And AST preset from application.yml and preset from config-as-code is truncated by the preset from b.yml "<preset_result>"
    And mutual parameter jira.project from config-as-code and from a.yml will be truncated by the "<jira.project>" from b.yml
    Examples:
      | branch | jira.project | preset_result |
      | test3  | jiraProjectB | presetYmlB    |
