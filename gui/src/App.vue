<template>
    <div>
        <nav class="navbar navbar-collapse navbar-light bg-light">
            <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNavAltMarkup"
                    aria-controls="navbarNavAltMarkup" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <a class="navbar-brand" href="/">
<!--                TODO: change the logo for Cassy-->
                <img class="img-fluid" src="./assets/logo_scalar.svg" height="150" width="150">
            </a>
            <div class="collapse navbar-collapse" id="navbarNavAltMarkup">
                <div class="navbar-nav">
                    <a class="nav-item nav-link active" href="/">View Clusters<span class="sr-only">(current)</span></a>
                    <a class="nav-item nav-link active" href="#">View Settings</a>
                </div>
            </div>
        </nav>
        <router-view/>
        <footer class="fixed-bottom">
            <div class="card-footer text-center text-muted">A backup tool for Cassandra, made by <a href="https://scalar-labs.com/">Scalar</a>.</div>
        </footer>
    </div>
</template>
