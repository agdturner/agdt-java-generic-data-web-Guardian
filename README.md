# Web Zoopla

https://github.com/agdturner/agdt-java-generic-data-web-Zoopla/

A Java tool for scraping and formatting data from Zoopla's Web site.

The Web site URL is: https://www.zoopla.co.uk/.

## Usages
1. Used for scraping house price paid from the Zoopla's website. Zoopla have made House Price Paid data available for a number of years since the organisation formed. Prioir to this the data was made available on houseprices.co.uk. It has been possible to get price made data and other information by requesting data from URLs with postcodes in them such as: https://www.zoopla.co.uk/house-prices/ls2-9jt/. This tool relies on the well formedness of this URLs.

## Code status and development roadmap
This code was abstracted from the Web library on which it depends in a code rationalisation in November 2019. Modifications are needed to the way the HTML response is parsed in order to produce the data that used to be produced by running the code in the past (prior to some changes Zoopla made for in the way the data is served out).

## Platform requirements
The cde has been compiled using Java 11. It may be possible to compile and run it on earlier version of the Java Standard Development Kit. 

## Dependencies
Please see the pom.xml for details.
1. Generic: https://github.com/agdturner/agdt-java-generic - A Java library with generally useful classes and methods.
2. Web: https://github.com/agdturner/agdt-java-generic-web/ - A Java library for processing data from and organising data for the World Wide Web.

## Contributions
Please raise issues and submit pull requests in the usual way. Contributions will be acknowledged.

## LICENCE
Please see the standard Apache 2.0 open source LICENCE.
