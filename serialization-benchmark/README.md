# Java Serialization Libraries Comparison

Benchmarks are executed as JUnit tests.

There are 3 benchmarks: `SmallObjectSerializationTest`, `MediumObjectSerializationTest`, `LargeObjectSerializationTest`.
In `src/test/resources` there are separate file for small, medium and large size objects. 
Objects are stored in JSON format. Each file contains 100 objects.
Number of iterations for each serialization framework benchmark is set to `10000` by default to minimize error.
On each iteration 100 objects are serializaed and deserialized.
As a result benchmarks provide arithmetic mean serialization time, arithmetic mean deserialization time and total output size of 100 objects.

To build project and execute benchmarks run:

```
mvn clean install
```

To change the number of iterations pass system property `serialization.iterations`:

```
mvn clean install -Dserialization.iterations=1000
```

## Test Data Generation

Objects for serialization benchmarks are stored in JSON format in files in test resources directory.

Test objects were created using [JSON Generator](http://www.json-generator.com/).

Small objects were created using template:

```js
[
  '{{repeat(100)}}',
  {
    id: '{{index()}}',
    name: '{{firstName()}} {{surname()}}',
    gender: '{{gender()}}',
    age: '{{integer(20, 40)}}',
    email: '{{email()}}',
    phone: '+1 {{phone()}}'
  }
]
```

Medium objects were created using template:

```js
[
  '{{repeat(100)}}',
  {
    id: '{{index()}}',
    guid: '{{guid()}}',
    isActive: '{{bool()}}',
    balance: '{{floating(1000, 4000, 2, "$0,0.00")}}',
    picture: 'http://placehold.it/32x32',
    age: '{{integer(20, 40)}}',
    eyeColor: '{{random("blue", "brown", "green")}}',
    name: '{{firstName()}} {{surname()}}',
    gender: '{{gender()}}',
    company: '{{company().toUpperCase()}}',
    email: '{{email()}}',
    phone: '+1 {{phone()}}',
    address: '{{integer(100, 999)}} {{street()}}, {{city()}}, {{state()}}, {{integer(100, 10000)}}',
    about: '{{lorem(1, "paragraphs")}}',
    registered: '{{date(new Date(2014, 0, 1), new Date(), "YYYY-MM-ddThh:mm:ss Z")}}',
    latitude: '{{floating(-90.000001, 90)}}',
    longitude: '{{floating(-180.000001, 180)}}',
    tags: [
      '{{repeat(7)}}',
      '{{lorem(1, "words")}}'
    ],
    friends: [
      '{{repeat(3)}}',
      {
        id: '{{index()}}',
        name: '{{firstName()}} {{surname()}}'
      }
    ],
    greeting: function (tags) {
      return 'Hello, ' + this.name + '! You have ' + tags.integer(1, 10) + ' unread messages.';
    },
    favoriteFruit: function (tags) {
      var fruits = ['apple', 'banana', 'strawberry'];
      return fruits[tags.integer(0, fruits.length - 1)];
    }
  }
]
```

Large objects were created using template:

```js
[
  '{{repeat(100)}}',
  {
    id: '{{index()}}',
    guid: '{{guid()}}',
    isActive: '{{bool()}}',
    balance: '{{floating(1000, 4000, 2, "$0,0.00")}}',
    picture: 'http://placehold.it/32x32',
    age: '{{integer(20, 40)}}',
    name: '{{firstName()}} {{surname()}}',
    gender: '{{gender()}}',
    company: '{{company().toUpperCase()}}',
    email: '{{email()}}',
    phone: '+1 {{phone()}}',
    address: '{{integer(100, 999)}} {{street()}}, {{city()}}, {{state()}}, {{integer(100, 10000)}}',
    about: '{{lorem(1, "paragraphs")}}',
    registered: '{{date(new Date(2014, 0, 1), new Date(), "YYYY-MM-ddThh:mm:ss Z")}}',
    latitude: '{{floating(-90.000001, 90)}}',
    longitude: '{{floating(-180.000001, 180)}}',
    followers: [
      '{{repeat(100, 300)}}',
      {
        id: '{{index()}}',
        name: '{{firstName()}} {{surname()}}'
      }
    ],
    posts: [
      '{{repeat(20, 30)}}',
      {
        id: '{{index()}}',
        title: '{{lorem(10, "words")}}',
        published: '{{date(new Date(2014, 0, 1), new Date(), "YYYY-MM-ddThh:mm:ss Z")}}',
        tags: [
          '{{repeat(5, 10)}}',
          '{{lorem(1, "words")}}'
        ],
        content: '{{lorem(10, "paragraphs")}}'
      }
    ]
  }
]
```
