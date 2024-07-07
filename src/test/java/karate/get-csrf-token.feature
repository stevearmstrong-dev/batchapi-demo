Feature: Get CSRF Token

  Scenario: Retrieve CSRF token
    Given url baseUrl
    When method GET
    Then status 200
    * def csrfToken = responseHeaders['X-CSRF-TOKEN'][0]