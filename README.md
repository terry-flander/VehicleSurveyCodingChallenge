# Vehicle Summary Coding Challenge
## Aconex -- Terry Flander

The application VehicleSurveyCodingChallenge is designed to take as its input Vehicle Counter data in the format where the first character of the input is the hose which was triggered ("A" or "B") followed by a timecode which is the number of milliseconds from midnight.

## Install

* clone from github
* Unpack in work directory
* cd to <work>/VehicleSurveyCodingChallenge

## Build

* mvn clean package

This will compile the source and run the test package using the included test file 'Vehicle Survey Coding Challenge sample data.txt'. Output will be generated in the 'output' directory which will be created automatically. The output files are named for their contents, i.e. test_am_pm.csv, test_1_hour.csv, test_half_hour.csv, test_20_minutes.csv, test_15_minutes.csv.

Each file contains the following information:
Day Number, Hour, Minute, North Count, South Count, North Speed (km/hr), South Speed (km/hr), North Separation (M), South Separation (M), North Peak, South Peak

## Testing

The suite of tests can be re-run at any time with:

* mvn exec:java -Dexec.args="test ."

## Run

The application expects at least two arguments: Input file name (including relative directory), and the Output directory. If the output directory does not exist, the program will create it. If the optional third argement 'average' is supplied, the output files will contain the averages of all values for all days in the input file.

E.g.
* mvn exec:java -Dexec.args="Vehicle_Survey_Coding_Challenge_sample_data.txt tmp"

This will run the VehicleSurveyCodingChallenge main class and produce output CSV files the Output directory 'tmp'. As this file is identical to the original test data, the output in that directory should match the output created during 'test' processing in the 'output' directory.
