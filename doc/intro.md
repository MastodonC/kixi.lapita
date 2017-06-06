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
* Read data as Clojure data structure: `csv-to-maps-coll`
* Read data as a `core.matrix` dataset: `csv-to-dataset`
This function can be used without schema coercion or with schema coercion.
When schema coercion fails it returns a data structure containing the error message(s). This means that it's easy to miss the coercion fail and work with broken data.
Here we try to give more flexibility and allow to continue working on the data whether it coerces or not, while being aware of when the coercion failed.
When used with a schema, the default behaviour is to return the whole data where whatever passed the coercion is coerced and the rest is still in its initial state.
There are options to see the error message, to write the uncoerced data to a file, or to return the coerce data without the uncoerced data.

#### Data preview functions
* Provides information on the data: `info`
This function returns a map containing the columns names, the number of columns and the number of rows.
* Have a pick at the data: `head`
Like in R or Python/Pandas, it can be useful while working on data to regularly check the transformations work by have a look at a small part of the dataset.
Here the default is to return the first five rows, but the number of rows to be returned can be customised.

### `kixi.lapita.plot` namespace

## What needs to be done?

## Try it!
