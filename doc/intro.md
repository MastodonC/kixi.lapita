# Introduction to kixi.lapita

## Content
* [Project background](#project-background)
* [What has been done?](#what-has-been-done)
  * [`kixi.lapita` namespace](#kixi-lapita-namespace)
  * [`kixi.lapita.plot` namespace](#kixi-lapita-plot-namespace)
* [What needs to be done?](#what-needs-to-be-done)
* [Try it!](#try-it)

## Project background
When it comes to data analysis (manipulating datasets and plotting data) we have no set tool at Mastodon C.
A lot of people tend to use R or sometimes Python. But as we're all proficient in Clojure, it makes sense for us to use this language.

We haven't identified a Clojure library that would suit our needs except from Incanter. And we are worried that Incanter is not fully maintained anymore.
Therefore instead of maintaining a rather large code base, we wish to create our own small library to use internally and to be used by other Clojurians.

This project was started in early 2017 in parallel with a client's project that would have been a good use case for it.
But so far due to time constraint, only basic capabilities have been added to `kixi.lapita`.

## What has been done?
At the moment there are functions to manipulate data in the namespace [kixi.lapita](https://github.com/MastodonC/kixi.lapita/blob/master/src/kixi/lapita.clj), and there are functions to plot data in the namespace [kixi.lapita.plot](https://github.com/MastodonC/kixi.lapita/blob/master/src/kixi/lapita/plot.clj).
The third namespace [kixi.lapita.schemas](https://github.com/MastodonC/kixi.lapita/blob/master/src/kixi/lapita/schemas.clj) is used within `kixi.lapita` namespace.

### `kixi.lapita` namespace

#### External dependencies
* The expected file format is comma-separated values (CSV) that we handle using the library [clojure.data.csv](https://github.com/clojure/data.csv).
* We use the Clojure I/O library [clojure.java.io](https://clojure.github.io/clojure/clojure.java.io-api.html).
* Despite the recent popularity of [clojure.spec](https://clojure.org/news/2016/05/23/introducing-clojure-spec) we are currently using schema coercion with `schema.coerce` from [plumatic/chema](https://github.com/plumatic/schema).
* We currently prioritise working with [`core.matrix`](https://github.com/mikera/core.matrix) data structures over classic Clojure data structures (like collections of maps). For that we use [clojure.core.matrix](https://github.com/mikera/core.matrix/blob/develop/src/main/clojure/clojure/core/matrix.cljc) and [clojure.core.matrix.dataset](https://github.com/mikera/core.matrix/blob/develop/src/main/clojure/clojure/core/matrix/dataset.clj).
* To manipulate core.matrix datasets, on top of the functions available in the library we use functions from [`witan.datasets`](https://github.com/MastodonC/witan.workspace-api/blob/master/src/witan/datasets.clj) developed at Mastodon C.

#### I/O related functions
* Write data to file: `write-csv!`
* Read data as collection of maps: `csv-to-maps-coll`
* Read data as a `core.matrix` dataset: `csv-to-dataset`


### `kixi.lapita.plot` namespace

## What needs to be done?

## Try it!
