Feature: Batch API operations

  Background:
    * def baseUrl = 'http://localhost:8080'
    * def user1 = { id: 1, name: 'John', email: 'john@example.com', age: 25, city: 'New York' }
    * def user2 = { id: 2, name: 'Jane', email: 'jane@example.com', age: 30, city: 'London' }

  Scenario: Perform batch PATCH operations
    Given url baseUrl
    And path '/api/batch/users'
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
    And match response[0].email == 'john@example.com'
    And match response[0].city == 'New York'
    And match response[1].name == 'Jane'
    And match response[1].email == 'jane@example.com'
    And match response[1].age == 30
    And match response[1].city == 'Paris'

  Scenario: Attempt to update non-existent user
    Given url baseUrl
    And path '/api/batch/users'
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
    And match response[0] == 'User not found: 999'