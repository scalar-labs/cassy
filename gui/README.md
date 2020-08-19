# Cassy GUI

## Project setup
```
npm install
```

### To run the GUI with Cassy
See the [Getting Started](../docs/getting-started.md) to set up your Cassy configuration and run the server.

Once you have started Cassy, go to the root folder and run the following commands to build and start the HTTP/1.1 server.
```
go build entrypoint.go
```
```
./entrypoint
```

In your browser, you can go to [localhost:8080](http://localhost:8080) to view the gui.

### Running the GUI in development mode
You can run the GUI on a development server to gain the benefits of hot reload. To do this, first navigate to the [router file](src/api/index.js). 
This file contains the configuration for Axios, which is used for talking with the backend server.

When running in production mode, the GUI is run using a file server, so it can exist on the same port as the backend (:8080). In development mode, the dev server will conflict with this, so we need to change the `baseUrl` to port 8090.

```javascript
export const api = axios.create({
  baseURL: 'http://localhost:8090/v1', // by default the port is set to 8080.
});
```

After making this change, we can start the Cassy server as usual, and then run `entrypoint` in dev mode.
```
./entrypoint -mode=dev
```
This will start the HTTP server at port 8090, and allow CORS.

Finally, we can start the GUI dev server by navigating back to the `gui` folder and running:
```
npm run serve
```

You should now be able to edit the GUI code and see live changes in your browser at `http://localhost:8080`.

#### Making a production build
After making your desired changes to the GUI, you can create a static webpage by running `npm run build`, which will create the files in `gui/dist`.
Make sure to go back to the `src/api/index.js` file and change the port back to `8080` before doing this.
