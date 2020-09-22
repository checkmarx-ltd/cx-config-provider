Feature: Configuration provider external apis tests
  
    #      - Application is started with GITHUB token in application-test-api.yml
    #      - and GITHUB token is defined in environment
    #      - and config provider is initialized by loading application-test-api.yml
    #      - and env properties are loaded using config provider
  Scenario Outline: Config provider loads data of a GITHUB_TOKEN env property and from application.yml 
                    and env variable overrides application.yml property
    Given env variable overrides application.yml property
    Then GITHUB token from application-test-api.yml is overridden by "<github_token>" loaded from the env variables
    Examples:
      | github_token       | 
      | envPropGitHubToken |      


  Scenario Outline: initial reader is a result of several resources: application.yml, env variables and application-secrets.yml
    Given application.yml, env variables and application-secrets.yml are loaded into initial resource using MultipleResourcesImpl
    Then GITHUB token from application-test-api.yml is overridden by "<github_token>" loaded from the env variables
    And AST token "<ast_token>" and AST preset "<ast_preset>" will be taken from application-secrets.yml
    And the order of override will be based on the order of the files added to the MultipleResourcesImpl

    Examples:
      | github_token       | ast_token| ast_preset|
      | envPropGitHubToken | astTokenFromSecrets| presetFromAppSecretsYml|
  
   #   - Application is started with GITHUB token defined in environment
   #   - and GITHUB token application-test-api.yml
   #   - and config provider is initialized by loading environment
   #   - and application-test-api.yml is loaded using config provider
  Scenario Outline: application.yml properties over GITHUB_TOKEN from env variable
    Given application.yml properties overrides env variables
    Then GITHUB token from env variables is overridden by the "<github_token>" from application.yml
    Examples:
      | github_token       |
      | githubTokenFromApp |


#  - application is started with GITHUB token and AST parameters in application-test-api.yml
#  - and GITHUB token is defined in env variables
#  - github repository contains config-as-code with AST preset
#  - and config provider is initialized by loading application-test-api.yml
#  - and config provider is initialized by loading env variables
#  - and config provider is initialized with github repository
  Scenario Outline: github config-as-code(GITHUB) over env variables over application.yml
     Given github config-as-code(GITHUB) over env variables over application.yml in branch "<branch>"
    Then AST preset from application.yml is overridden by the preset from config-as-code "<preset_result>"
    Examples:
      | branch | preset_result          | 
      | test1  | presetFromConfigAsCode |

#  - application is started with GITHUB token and AST parameters in application-test-api.yml
#  - and GITHUB token is defined in environment
#  - github repository contains config-as-code with AST preset
#  - and github repository contains also config-as-code.yml in the .checkmarx folder
#  - and config provider is initialized by loading application-test-api.yml and env variables
#  - and config provider is initialized with github repository
  Scenario Outline: github config-as-code.yml over config-as-code(GITHUB) over env variables over application.yml
    Given config-as-code.yml over config-as-code(GITHUB) over env variables over application.yml in github branch "<branch>"
    Then AST preset from application.yml and preset from config-as-code is overridden by the preset from config-as-code.yml "<preset_result>"
    Examples:
      | branch | preset_result       |
      | test2  | presetFromGithubYml |

#  - application is started with GITHUB token and AST parameters in application-test-api.yml
#  - and GITHUB token is defined in environment
#  - and config-as-code file name is defined in application.yml as cx.configuration
#  - github repository contains cx.configuration with AST preset
#  - github repository contains .checkmarx folder with a.yml and b.yml
#  - and they both contain contains AST preset field and additional unique fields
#  - and config provider is initialized by loading application-test-api.yml and env variables
#  - and config provider is initialized with github repository
  Scenario Outline: b.yml(GITHUB) over a.yml(GITHUB) over config-as-code(GITHUB) over env variables over application.yml
     Given in github branch "<branch>": b.yml(GITHUB) over a.yml(GITHUB) over config-as-code(GITHUB) over env variables over application.yml
    Then unique elements form all configuration file will exist in in the final configuration
    And AST preset from application.yml and preset from config-as-code is overridden by the preset from b.yml "<preset_result>"
    And mutual parameter jira.project from config-as-code and from a.yml will be overridden by the "<jira.project>" from b.yml
    Examples:
      | branch | jira.project | preset_result |
      | test3  | jiraProjectB | presetYmlB    |

    
  Scenario Outline: Config provider data from application.yml and then 
                    and it loads all environment variables defined in the operation system,
                    therefore env variables override application.yml properties
    Given Config provider loads all environment variables and then data from application.yml
    Then "<path>" env variable will override the one from application-test-api.yml
    Examples:
      | path | 
      | path | 