This repository contains automated test scripts for WebstaurantStore using Selenium WebDriver. The purpose of these tests is to simulate and validate user interactions with the WebstaurantStore website, focusing on functionalities such as searching for products, adding items to the cart, and verifying cart operations.

## Prerequisites

Before you run the tests, make sure you have the following installed:
- Java JDK (most recent stable version)
- Maven (for managing dependencies and running the tests)

## Getting Started

To set up your local environment with the necessary dependencies:
1. Clone this repository.
2. Navigate to the project directory and run `mvn clean install` to install dependencies.

## Test Scenarios

The tests cover the following scenarios:
1. Searching for a specific query and validating that all search results contain the keyword "Table" in their titles.
2. Adding the last item found in the search results to the cart.
3. Emptying the cart and verifying that the cart is empty.

## Running Tests

To run the tests, execute the following command in your terminal:
'mvn test'

## Structure

Here is a brief overview of the main components in this project:
- **`WebstaurantStoreTest.java`**: Contains all test cases and utility methods for navigating the site, adding items to the cart, and more.

## License

Distributed under the MIT License. See `LICENSE` for more information.

## Acknowledgments

- Selenium WebDriver team.
- WebDriverManager (Boni Garcia)

## Author

- **Ben Krack** - Initial work and maintenance.

Feel free to reach out with any questions or feedback!
