# kixi.lapita

[![CircleCI](https://circleci.com/gh/MastodonC/kixi.lapita.svg?style=svg)](https://circleci.com/gh/MastodonC/kixi.lapita)
[![Dependencies Status](https://jarkeeper.com/MastodonC/kixi.lapita/status.svg)](https://jarkeeper.com/MastodonC/kixi.lapita)

## For (data) explorers!

The Lapita was a prehistoric Pacific Ocean people from c. 1600 BCE to c. 500 BCE .
They were expert in seamanship and navigation, reaching out and finding islands separated from each other by hundreds of miles of empty ocean. Their descendants, the Polynesians, would populate islands from Hawaii to Easter Island, possibly even reaching the South American continent.

See [wikipedia](https://en.wikipedia.org/wiki/Lapita_culture)

**`Kixi.lapita` is a Clojure library to preview, transform and plot your data.**

### Status

See progress:  [![Stories in progress](https://badge.waffle.io/MastodonC/kixi.lapita.svg?label=in%20progress&title=In%20progress)](http://waffle.io/MastodonC/kixi.lapita)

There are currently functions to:
* read/write files to/from [`core.matrix` datasets](https://github.com/mikera/core.matrix/blob/develop/src/main/clojure/clojure/core/matrix/dataset.clj)
* preview the content of a dataset
* transform data in a dataset
* plot data with a vertical bar chart

### Features
For more details, read the [documentation](doc/intro.md).

This project uses `core.matrix` datasets as a data structure to handle the data.

It uses functions from [`clojure.core.matrix.dataset`](https://github.com/mikera/core.matrix/blob/develop/src/main/clojure/clojure/core/matrix/dataset.clj) and [`witan.datasets`](https://github.com/MastodonC/witan.workspace-api/blob/master/src/witan/datasets.clj).


#### Read/write files to/from datasets

* `csv-to-maps-coll` : To load data from a CSV file into a collection of maps
* `csv-to-dataset` : To load data from a CSV file into a `core.matrix` dataset with or w/o schema coercion
* `write-csv!` : To write the content of a dataset to a CSV file

#### Preview the content of a dataset

* `head` : To have a pick at the n top rows
* `info` : To know the column names, number of rows and columns
* `describe` : [work in progress] To describe the numerical columns (count, min, max...)

#### Transform data in a dataset

* `count-elements-in-column` : To output the count of each element in a column

#### Visualise data with a bar chart

* `plot-bar-chart` : To plot data from the columns of a dataset. It outputs a svg file.

### Installation

At the moment `kixi.lapita` is not available on Clojars, but you can clone the project to try it locally. See [instructions](doc/intro.md#try-kixilapita).

### Contribute

* Source code: [github.com/MastodonC/kixi.lapita](https://github.com/MastodonC/kixi.lapita)
* Issue tracker: [github.com/MastodonC/kixi.lapita/issues](https://github.com/MastodonC/kixi.lapita/issues)
* Waffle board: [waffle.io/MastodonC/kixi.lapita](https://waffle.io/MastodonC/kixi.lapita)

### License

Copyright © 2017 Mastodon C Ltd

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
