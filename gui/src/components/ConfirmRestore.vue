<template>
    <div class="modal fade" tabindex="-1" id="confirmRestore" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Confirm Restore?</h5>
                    <button class="close" type="button" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <p>Click the button below to confirm you want to restore from this backup.</p>
                </div>
                <small v-if="failed" class="form-text text-danger text-center">Error: {{ error }}</small>
                <div class="modal-footer">
                    <button class="btn btn-outline-secondary mx-1" @click="returnToRestoreCluster()">Cancel
                    </button>
                    <button class="btn btn-primary mx-1"
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
      data: {},
    },
    data() {
      return {
        failed: false,
        error: '',
      };
    },
    mounted() {
      $('#confirmRestore').on('hide.bs.modal', () => {
        this.failed = false;
      });
    },
    methods: {
      returnToRestoreCluster() {
        $('#confirmRestore').modal('hide');
        $('#restoreCluster').modal('show');
      },
      restoreBackup() {
        this.$api.put(`clusters/${this.cluster_id}/data/${this.snapshot_id}`, this.data).then((response) => {
          if (response.status === 200) {
            $('#confirmRestore').modal('hide');
            this.$router.push({name: 'ViewRestores', params: { snapshot_id: this.snapshot_id}});
          }
        }).catch(error => {
          this.error = error.response.data.message;
          this.failed = true;
        });
      },
    },
  };
</script>
