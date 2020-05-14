<template>
    <div class="modal fade" tabindex="-1" id="registerCluster" aria-labelledby="registerClusterModal"
         aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Register a new cluster</h5>
                    <button class="close" type="button" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form>
                        <div class="form-group">
                            <label for="cassandraHostInput">Cassandra host address</label>
                            <input class="form-control" type="text" id="cassandraHostInput" ref="cassandraHostInput"
                                   placeholder="localhost">
                        </div>
                        <div class="modal-footer">
                            <button class="btn btn-primary" data-toggle="modal" data-target="#registerCluster"
                                    @click="registerCluster">Register
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
  import $ from 'jquery';

  export default {
    methods: {
      registerCluster() {
        this.$api.post('/clusters', {
          cassandra_host: document.getElementById('cassandraHostInput').value,
        }).then((response) => {
          if (response.status === 200) {
            this.$emit('updateClusterList');
          }
        });
      },
    },
    mounted() {
      $('#registerCluster').on('shown.bs.modal', function() {
        $(this).find('#cassandraHostInput').focus();
      });
    },
  };
</script>
