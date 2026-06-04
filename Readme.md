# Weather Information App

## Overview

The **Weather Information App** is a robust desktop application built in Java that provides real-time weather updates, interactive unit conversions, and historical search tracking.

Designed with a focus on clean architecture and an intuitive user experience, the application features a modern graphical user interface (GUI) that dynamically adapts its visual theme based on the time of day.

**Author:** Owden Magnusen

---

# Features

## Real-Time Weather Data

Displays accurate, up-to-the-minute weather conditions including:

* Temperature
* Feels Like Temperature
* Humidity
* Wind Speed
* Atmospheric Pressure
* Visibility
* Sunrise Time
* Sunset Time

---

## Modern GUI

Built using **Java Swing**, featuring custom-painted components such as rounded borders to deliver a polished, web-like user experience.

---

## Dynamic Time-of-Day Themes

The application background and color palette automatically adapt to the current time of day at the searched location.

Supported themes include:

* 🌅 Morning
* ☀️ Afternoon
* 🌇 Evening
* 🌙 Night

---

## Flexible Location Input

Users can retrieve weather data using either:

### City Name

Examples:

* Accra
* Kumasi
* Tamale

### Geographic Coordinates

* Latitude
* Longitude

---

## Interactive Unit Conversion

Instantly switch between measurement systems.

### Temperature Units

* Celsius (°C)
* Fahrenheit (°F)

### Wind Speed Units

* Kilometers per Hour (km/h)
* Miles per Hour (mph)
* Meters per Second (m/s)

---

## 5-Day Forecast

A dedicated forecast panel provides a detailed weather outlook for the next five days.

Each forecast entry includes:

* Weather condition icon
* Maximum temperature
* Minimum temperature
* Precipitation probability

---

## Search History Tracking

A persistent **Recent Searches** sidebar stores previously searched locations along with timestamps.

Benefits include:

* Quick access to previous searches
* Timestamped records
* Easy navigation

---

# How to Use the Application

## 1. Launch the Application

Run:

* `Main.java`

or

* The packaged executable file

This opens the Weather Information App window.

---

## 2. Search by City

1. Enter a city name in the **City** input field.
2. Click **Search**.

Example cities:

* Accra
* Kumasi
* Tamale

---

## 3. Search by Coordinates

1. Enter a valid **Latitude**.
2. Enter a valid **Longitude**.
3. Click **Search**.

---

## 4. Change Units

Use the radio buttons located below the search area to switch between:

### Temperature

* Celsius (°C)
* Fahrenheit (°F)

### Wind Speed

* km/h
* mph
* m/s

Weather information updates instantly.

---

## 5. View Search History

The left-side panel displays previous searches.

Use the **Clear** button to remove stored history.

---

## 6. Analyze the Forecast

Review the bottom dashboard panel to view weather predictions for the next five days.

---

# Technical Implementation Details

## API Integration

The application integrates with the OpenWeatherMap API to retrieve weather information using two primary endpoints.

### Current Weather Data API

Used to retrieve:

* Current temperature
* Humidity
* Pressure
* Wind speed
* Cloud coverage
* General weather conditions

### 5-Day / 3-Hour Forecast API

Used to generate the short-term forecast displayed within the application.

The forecast data is processed and organized into a user-friendly forecast panel.

### Networking

The application performs API requests asynchronously to maintain a smooth and responsive user interface.

Benefits include:

* No UI freezing
* Better responsiveness
* Improved user experience

---

## JSON Parsing

Weather data returned by OpenWeatherMap is provided in JSON format.

The application uses a dedicated JSON processing library such as:

* `org.json`
* `Gson`

### Parsing Process

JSON responses are mapped into custom Java model objects.

Data is extracted from:

* `main`
* `wind`
* `clouds`
* `sys`

JSON sections.

### Weather Icon Mapping

Weather condition codes are converted into visual weather icons.

Examples:

| Description  | Icon |
| ------------ | ---- |
| Clear Sky    | ☀️   |
| Light Rain   | 🌧️  |
| Cloudy       | ☁️   |
| Thunderstorm | ⛈️   |

---

# Application Architecture

The project follows modular software engineering principles and separates concerns into dedicated packages.

## `weatherapp.model`

Contains data models representing:

* Current weather data
* Forecast data
* Search history entries

---

## `weatherapp.service`

Handles:

* HTTP requests
* API integration
* JSON parsing
* Search history management

Key classes:

* `WeatherService`
* `SearchHistoryManager`

---

## `weatherapp.ui`

Contains all graphical user interface components.

Key classes:

* `WeatherAppFrame`
* `ForecastPanel`
* `ThemeManager`
* `RoundedBorder`

Responsibilities include:

* Window layout
* Component rendering
* Theme management
* Forecast visualization

---

## `weatherapp.util`

Contains helper and utility classes.

Examples:

* `TimeOfDayUtil`
* `UnitConverter`
* `WeatherIconUtil`

Responsibilities include:

* Temperature conversions
* Wind speed conversions
* Timestamp formatting
* Theme selection logic
* Weather icon mapping

---

# Error Handling

The application includes comprehensive error handling to improve reliability and user experience.

## Input Validation

Validates:

* Empty search fields
* Invalid city names
* Invalid coordinate values
* Coordinate boundary limits

---

## API and Network Errors

Gracefully handles:

* Network timeouts
* Invalid API keys
* Rate-limit restrictions
* Unknown locations (HTTP 404)
* Connection failures

Errors are displayed through user-friendly messages within the application interface rather than causing application crashes.

---

# Screenshots

The following screenshots demonstrate the dynamic theme system.

## Morning / Sunrise Theme
![Morning](https://owdenmagnusen.com/wp-content/uploads/2026/06/Morning-scaled.png)


Displays the soft gold theme used during morning searches.

---

## Afternoon Theme

**File:** `https://owdenmagnusen.com/wp-content/uploads/2026/06/Afternoon-scaled.png`
![Afternoon](https://owdenmagnusen.com/wp-content/uploads/2026/06/Afternoon-scaled.png)

Displays the vibrant blue daytime theme.

---

## Sunset / Late Afternoon Theme

**File:** `https://owdenmagnusen.com/wp-content/uploads/2026/06/SunsetLate-Afternoon-scaled.png`
![Sunset / Late Afternoon](https://owdenmagnusen.com/wp-content/uploads/2026/06/SunsetLate-Afternoon-scaled.png)
Displays the warm orange sunset theme.

---

## Night Theme

**File:** `https://owdenmagnusen.com/wp-content/uploads/2026/06/EveningMidnight-scaled.png`
![Night](https://owdenmagnusen.com/wp-content/uploads/2026/06/EveningMidnight-scaled.png)
Displays the dark indigo theme optimized for nighttime viewing.

---

# Technologies Used

* Java
* Java Swing
* OpenWeatherMap API
* JSON Parsing Library (`org.json` or `Gson`)
* HTTP Networking
* Object-Oriented Programming (OOP)

---

# Author

**Owden Magnusen**
