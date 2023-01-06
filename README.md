# octopus-go-watcher
Simple app to see what percentage of electricity consumption falls within the off peak window.

As my Octopus Go prices have just massively increased I'd like to keep an eye on how well I'm keeping consumption to 
the off peak period.

There's no frontend yet, just some json describing the total consumption and the percentage that was off peak and 
therefore cheap. At some point I might build a traffic light frontend.

Values are returned for the consumption over the last year, the last 30 days and yesterday.

The results are cached for 24 hours as they would be the same anyway.


## You will need to supply values to complete the config.

#### Your meter mpan
#### Your meter serial number
#### Your octopus api key value

You can use environment variables for these, or put them in a run configuration:

GO_WATCHER_METER_MPAN=...
GO_WATCHER_METER_SERIAL_NUMBER=...
GO_WATCHER_API_KEY=...

