<template>
    <div class="modal fade" tabindex="-1" id="confirmRestore" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered ">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Are you sure you want to restore?</h5>
                    <button class="close" type="button" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="row row-cols-2">
                        <p class="col">Restore Mode:</p>
                        <p class="col">{{typeName(data.restore_type)}}</p>
                        <p class="col">Snapshot ID:</p>
                        <p class="col">{{snapshot_id}}</p>
                        <p class="col">Target IP:</p>
                        <p class="col">{{targetIp}}</p>
                        <p class="col">Snapshot-only:</p>
                        <p class="col">{{snapshot_only}}</p>
                    </div>
                </div>
                <small v-if="failed" class="form-text text-danger text-center">Error: {{ error }}</small>
                <div class="modal-footer">
                    <button class="btn btn-outline-secondary mx-1" type="button" @click="returnToRestoreCluster()">Cancel
                    </button>
                    <button class="btn btn-primary mx-1"
                            type="button"
                            @click="restoreBackup"
                    >Confirm
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
  import $ from 'jquery';

  export default {
    props: {
      cluster_id: String,
      snapshot_id: String,
      data: Object,
    },
    data() {
      return {
        failed: false,
        error: '',
      };
    },
    computed: {
      targetIp: function() {
        if (this.data.target_ips) {
          return this.data.target_ips[0];
        }
        return "";
      },
      snapshot_only: function() {
        return !!this.data.snapshot_only;
      },
    },
    mounted() {
      $('#confirmRestore').on('hide.bs.modal', () => {
        this.failed = false;
      });
    },
    methods: {
      typeName(t) {
        return t === 2 ? 'Node' : 'Cluster';
      },
      returnToRestoreCluster() {
        $('#confirmRestore').modal('hide');
        $('#restoreCluster').modal('show');
      },
      restoreBackup() {
        this.$api.put(`clusters/${this.cluster_id}/data/${this.snapshot_id}`, this.data).then((response) => {
          if (response.status === 200) {
            $('#confirmRestore').modal('hide');
            this.$router.push({name: 'ViewRestores', params: {snapshot_id: this.snapshot_id}});
          }
        }).catch(error => {
          this.error = error.response.data.message;
          this.failed = true;
        });
      },
    },
  };
</script>
