Feature: Batch API operations

  Background:
    * url baseUrl
    * def user1 = { id: 1, name: 'John', email: 'john@example.com', age: 25, city: 'New York' }
    * def user2 = { id: 2, name: 'Jane', email: 'jane@example.com', age: 30, city: 'London' }
    * def getCsrfToken = call read('classpath:karate/get-csrf-token.feature')
    * def csrfToken = getCsrfToken.csrfToken

  Scenario: Perform batch PATCH operations
    Given path '/api/batch/users'
    And header X-CSRF-TOKEN = csrfToken
    And request
  """
  {
    "operations": [
      {
        "method": "PATCH",
        "url": "/api/users/1",
        "body": {
          "name": "John Updated",
          "age": 26
        }
      },
      {
        "method": "PATCH",
        "url": "/api/users/2",
        "body": {
          "city": "Paris"
        }
      }
    ]
  }
  """
    When method POST
    Then status 200
    And match response[0].name == 'John Updated'
    And match response[0].age == 26
    And match response[1].city == 'Paris'

  Scenario: Attempt to update non-existent user
    Given path '/api/batch/users'
    And header X-CSRF-TOKEN = csrfToken
    And request
  """
  {
    "operations": [
      {
        "method": "PATCH",
        "url": "/api/users/999",
        "body": {
          "name": "Non-existent User"
        }
      }
    ]
  }
  """
    When method POST
    Then status 200
    And match response[0] contains 'User not found: 999'