# Introduction to kixi.lapita

## Content
* [Project background](#project-background)
* [What has been done?](#what-has-been-done)
  * [`kixi.lapita` namespace](#kixilapita-namespace)
    * [dependencies for kixi.lapita](#dependencies-for-kixilapita)
	* [I/O related functions](#io-related-functions)
	* [Data preview functions](#data-preview-functions)
	* [Data transformation functions](#data-transformation-functions)
  * [`kixi.lapita.plot` namespace](#kixilapitaplot-namespace)
    * [dependencies for kixi.lapita.plot](#dependencies-for-kixilapitaplot)
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

#### Dependencies for `kixi.lapita`
* The expected file format is comma-separated values (CSV) that we handle using the library [clojure.data.csv](https://github.com/clojure/data.csv).
* We use the Clojure I/O library [clojure.java.io](https://clojure.github.io/clojure/clojure.java.io-api.html).
* Despite the recent popularity of [clojure.spec](https://clojure.org/news/2016/05/23/introducing-clojure-spec) we are currently using schema coercion with `schema.coerce` from [plumatic/chema](https://github.com/plumatic/schema).
* We currently prioritise working with [`core.matrix`](https://github.com/mikera/core.matrix) data structures over classic Clojure data structures (like collections of maps). For that we use [clojure.core.matrix](https://github.com/mikera/core.matrix/blob/develop/src/main/clojure/clojure/core/matrix.cljc) and [clojure.core.matrix.dataset](https://github.com/mikera/core.matrix/blob/develop/src/main/clojure/clojure/core/matrix/dataset.clj).
* To manipulate core.matrix datasets, on top of the functions available in the library we use functions from [`witan.datasets`](https://github.com/MastodonC/witan.workspace-api/blob/master/src/witan/datasets.clj) developed at Mastodon C.

#### I/O related functions
* Write data to file: `write-csv!`
```Clojure
> (write-csv! test-dataset "test-file.csv")
```
* Read data as Clojure data structure: `csv-to-maps-coll`
```Clojure
> (csv-to-maps-coll "test-file.csv")

({:col-1 "1", :col-2 "a", :col-3 "1.1"}
 {:col-1 "2", :col-2 "b", :col-3 "1.2"}
 {:col-1 "3", :col-2 "c", :col-3 "1.3"})
```
* Read data as a `core.matrix` dataset: `csv-to-dataset`
This function can be used without schema coercion or with schema coercion.
When schema coercion fails it returns a data structure containing the error message(s). This means that it's easy to miss the coercion fail and work with broken data.
Here we try to give more flexibility and allow to continue working on the data whether it coerces or not, while being aware of when the coercion failed.
When used with a schema, the default behaviour is to return the whole data where whatever passed the coercion is coerced and the rest is still in its initial state.
There are options to see the error message, to write the uncoerced data to a file, or to return the coerce data without the uncoerced data.
```Clojure
> (csv-to-dataset "test-file.csv")

| :col-1 | :col-2 | :col-3 |
|--------+--------+--------|
|      1 |      a |    1.1 |
|      2 |      b |    1.2 |
|      3 |      c |    1.3 |

```

#### Data preview functions
* Provides information on the data: `info`
This function returns a map containing the columns names, the number of columns and the number of rows.
```Clojure
> (info test-ds)

{:column-names [:col-1 :col-2 :col-3], :num-rows 3, :num-columns 3}
```
* Have a pick at the data: `head`
Like in R or Python/Pandas, it can be useful while working on data to regularly check the transformations work by have a look at a small part of the dataset.
Here the default is to return the first five rows, but the number of rows to be returned can be customised.
```Clojure
> (head test-ds 1)

| :col-1 | :col-2 | :col-3 |
|--------+--------+--------|
|      1 |      a |    1.1 |
```

#### Data transformation functions
`count-elements-in-column` isn't particularly well named, but it's role is, for a given column, to count the number of items for each type of items in this column.
It takes in a dataset and outputs a new dataset with the specified column name and a count column.

### `kixi.lapita.plot` namespace
#### Dependencies for `kixi.lapita.plot`
To create plots we wanted to use a sub-library within the [thi.ng](http://thi.ng/) project as they are powerful and flexible.
Here [`thi.ng/geom`](https://github.com/thi-ng/geom) is better suited for the purpose of creating plots. It is a plotting library that gives us tools to create plotting tools, but it doesn't come ready out of the box.

#### Plotting function
For the moment there's one function to create a vertical bar chart: `plot-bar-chart`.
This function expects data as a core.matrix dataset, columns names for the x-axis values and the y-axis values and the name for the resulting plot file.
There are additional optional options to customise the colour, the width and height of the plot.

## What needs to be done?

## Try it!
