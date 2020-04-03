<template>
    <div class="modal fade" tabindex="-1" id="registerBackup" aria-labelledby="registerBackupModal" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Start a new backup</h5>
                    <button class="close" type="button" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form>
                        <div class="form-group">
                            <label for="backupTypeSelect">Select a backup type</label>
                            <select class="custom-select" id="backupTypeSelect">
                                <option selected>Backup Type</option>
                                <option value="1">Type 1</option>
                                <option value="2">Type 2</option>
                                <option value="3">Type 3</option>
                            </select>
                        </div>
                        <div class="modal-footer">
                            <button class="btn btn-primary" data-toggle="modal" data-target="#registerBackup"
                                    @click="createBackup">Create Backup
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
      clusters: {
        type: Object,
      },
      cluster_id: String
    },
    methods: {
      createBackup() {
        this.$api.post(`clusters/${this.cluster_id}/backups`, {
          cassandra_host: '127.0.0.1',
          backup_type: document.getElementById('backupTypeSelect').value,
        });
      },
    },
  };
</script>
