<template>
    <div>
        <nav class="navbar fixed-top navbar-light bg-light p-0 flex-md-nowrap py-1 shadow-sm">
            <a class="navbar-brand col-sm-3 col-md-2 mr-0" href="/">
                <!--                TODO: change the logo for Cassy-->
                <img class="img-fluid" src="./assets/logo_scalar.svg" height="115" width="115">
            </a>
        </nav>
        <div class="container-fluid">
            <div class="row">
                <nav class="col-sm-0 d-none d-md-block px-0 bg-light sidebar">
                    <div class="sidebar-sticky pt-2">
                       <ul class="nav flex-column">
                           <li class="nav-item">
                               <a class="nav-link" href="#">
                                   View Clusters
                               </a>
                           </li>
                           <li class="nav-item">
                               <a class="nav-link" href="#">
                                   View Settings
                               </a>
                           </li>
                       </ul>
                    </div>
                </nav>
                <div class="col-md-10 ml-auto mr-5 pl-0 pt-5">
                <router-view/>
                </div>
            </div>
        </div>
        <footer class="">
            <div class="card-footer text-center bg-light text-muted">A backup tool for Cassandra, made by <a
                    href="https://scalar-labs.com/">Scalar</a>.
            </div>
        </footer>
    </div>
</template>

<script>
  export default {
    mounted: function() {
      this.$nextTick(function() {
        document.title = 'Cassy';
      });
    },
  };
</script>

<style>
    .sidebar {
        position: fixed;
        top: 0;
        bottom: 0;
        left: 0;
        z-index: 100;
        padding: 48px 0 0;
        box-shadow: inset -1px 0 0 rgba(0, 0, 0, .1);
    }
    .sidebar-sticky {
        position: -webkit-sticky;
        position: sticky;
    }
    .sidebar .nav-link {
        font-weight: 500;
        color: #333;
    }
</style>
