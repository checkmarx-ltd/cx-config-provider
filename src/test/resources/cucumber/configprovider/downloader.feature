Feature: Configuration provider git path tests
    
    @Skip
    Scenario Outline: repository source is GITHUB
    When repository source is GITHUB
    Then configuration provider will retrieve the configuration "<path>" from repository
    And the the returned configuration object will be "<expected>"
    And exception will be "<exception_thrown>"

    Examples:
        | path                          | expected  | exception_thrown |
        # directory With Single File
        | .checkmarx                    | populated | false            |
        # deep directory hierarchy
        | deep/directory/structure      | populated | false            |
        # non existing path
        | inexistence                   | empty     | false            |
        # file instead of directory
        | .checkmarx/config-as-code.yml | empty     | false            |
        # directory without files and empty content
        | deep/directory                | empty     | false            |
        # directory without files and empty content
        | directory-with-empty-file     | empty     | false            |
        # directory with multiple files
        | config-as-code-test           | empty     | true             |
