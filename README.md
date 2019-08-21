# TuCAN_Exercise

Your task is to write a recorder and player of pen and finger events for Android devices.

A sample CSV file is saved in [./TuCAN.csv](https://github.com/Heikofant/TuCAN_Exercise/blob/master/TUCAN.csv).

## Recorder
* The recorder should record ALL touch and hover events, including historical events.
* Record one "line" for each event, both "main" and "historical".
  * The variables that need to be stored are listed in the exemplary [CSV](https://github.com/Heikofant/TuCAN_Exercise/blob/master/TUCAN.csv) file.

* Additionally, store the generated SPD-file.

<i>Optionally:</i> store the data not only in a CSV file but in a database of your choice.

## Player

The player should be able to replay a stored SPD-file and/or the custom CSV file.
* Distinguish between touch and hover events in color
* Add a pause/play button
