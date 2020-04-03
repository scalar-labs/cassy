<template>
    <div class="modal fade" tabindex="-1" id="restoreCluster" aria-labelledby="restoreClusterModal" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Restore Backup</h5>
                    <button class="close" type="button" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form>
                        <div class="form-group">
                            <label for="restoreTypeSelect">Select a restore type</label>
                            <select class="custom-select" id="restoreTypeSelect">
                                <option selected>Restore Type</option>
                                <option value="1">Type 1</option>
                                <option value="2">Type 2</option>
                            </select>
                        </div>
                        <div class="modal-footer">
                            <button class="btn btn-primary" data-toggle="modal" data-target="#restoreCluster"
                                    @click="restoreBackup()">Restore Backup
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
  export default {
    props: {
      cluster_id: String,
      snapshot_id: String
    },
    methods: {
      restoreBackup() {
        this.$api.put(`clusters/${this.cluster_id}/data/${this.snapshot_id}`, {
          restore_type: document.getElementById('restoreTypeSelect').value
        });
      },
    },
  };
</script>
