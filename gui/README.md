# Cassy

## Project setup
```
npm install
```

### To run the GUI with Cassy
See the [Getting Started](../docs/getting-started.md) to set up your Cassy configuration and run the server.

Once you have started Cassy, go to the root folder and run the following command to start the HTTP/1.1 server.
```
./entrypoint -mode=dev
```
Next, start the development server by navigating to the [gui](../gui) folder and executing the following command.
```
npm run serve
```
If you have followed the steps correctly, you should have
1) A running `cassy-server`
2) A running `entrypoint` on localhost:8090
3) A running dev server on localhost:8080

In your browser, you can go to [localhost:8080](http://localhost:8080) to view the gui.

### Compiles and minifies for production
```
npm run build
```

### Lints and fixes files
```
npm run lint
```

### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).
