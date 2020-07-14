<template>
    <div>
        <nav class="navbar fixed-top navbar-light bg-light p-0 flex-md-nowrap py-1 shadow-sm">
            <a class="navbar-brand col-sm-3 col-md-2 mr-0" href="/">
                Cassy
            </a>
        </nav>
        <div class="container-fluid">
            <div class="row">
                <nav class="collapse col-md-1 d-lg-none d-xl-block px-0 bg-light sidebar" id="sidebar">
                    <div class="sidebar-sticky pt-2">
                        <ul class="nav flex-column">
                            <li class="nav-item">
                                <router-link class="nav-link" to="/#">
                                    View Clusters
                                </router-link>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="#">
                                    View Settings
                                </a>
                            </li>
                        </ul>
                    </div>
                </nav>
                <div class="col d-flex mx-3 mb-5 pl-0 pt-5 justify-content-center">
                    <router-view :error_message="error_message" @showError="toggleErrorNotification($event)"/>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
  import $ from 'jquery';

  export default {
    data() {
      return {
        error_message: "",
      }
    },
    mounted: function() {
      this.$nextTick(function() {
        document.title = 'Cassy';
      });

      $('#sidebarCollapse').on('click', function() {
        $('#sidebar').toggleClass('disabled');
      });
    },
    methods: {
      toggleErrorNotification(event) {
        this.error_message = event;
        $('#errorNotificationToast').toast('show');
      }
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
        min-width: 150px;
        padding: 48px 0 0;
        box-shadow: inset -1px 0 0 rgba(0, 0, 0, .1);
        transition: all 0.3s;
        min-height: 100vh;
    }

    .sidebar-sticky {
        position: -webkit-sticky;
        position: sticky;
    }

    .sidebar .nav-link {
        font-weight: 500;
        color: #333;
    }

    .sidebar.disabled {
        margin-left: -150px;
    }
</style>
