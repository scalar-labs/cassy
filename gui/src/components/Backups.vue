<template>
    <div>
        <h4>Backups</h4>
        <table class="table table-borderless table-striped text-nowrap text-center">
            <thead>
            <tr>
                <th scope="col">Snapshot ID</th>
                <th scope="col">Target IP</th>
                <th scope="col">Created At</th>
                <th scope="col">Updated At</th>
                <th scope="col">Backup Type</th>
                <th scope="col">Status</th>
            </tr>
            </thead>
            <tbody>
            <tr
                    v-for="(b, index) in backups.entries"
                    :key="index"
            >
                <td>{{b.snapshot_id}}</td>
                <td>{{b.target_ip}}</td>
                <td>{{ parseInt(b.created_at) | moment('YYYY/M/d, h:mm a')}}</td>
                <td>{{ parseInt(b.updated_at) | moment('YYYY/M/d h:mm a')}}</td>
                <td>{{b.backup_type}}</td>
                <td>{{b.status}}</td>
                <td>
                    <button
                            type="button"
                            class="btn btn-secondary"
                            data-toggle="modal"
                            data-target="#restoreCluster"
                            @click="$emit('emitSnapshotId', b.snapshot_id)"
                    >Restore
                    </button>
                </td>
            </tr>
            </tbody>
        </table>
        <div class="row justify-content-end">
            <button class="btn btn-primary" data-toggle="modal" data-target="#registerBackup">Create Backup</button>
        </div>
    </div>
</template>

<script>
  export default {
    props: {
      backups: {
        type: Object,
      },
    },
  };
</script>
