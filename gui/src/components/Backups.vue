<template>
    <div>
        <h5>Backups in Cluster {{cluster.cluster_id}}</h5>
        <div class="bg-secondary text-white border rounded my-3">
            <p class="m-2">CLUSTER_ID: {{cluster.cluster_id}}</p>
            <p class="m-2">TARGET_IPS: {{cluster.target_ips}}</p>
            <p class="m-2">KEYSPACES: {{cluster.keyspaces}}</p>
        </div>
        <table class="table text-nowrap text-center">
            <thead>
            <tr>
                <th scope="col">Snapshot ID</th>
                <th scope="col">Backup Type</th>
                <th scope="col">Target IP</th>
                <th scope="col">Status</th>
                <th scope="col"></th>
            </tr>
            </thead>
            <tbody v-for="(b, i) in backups_by_snapshot" :key="i" class="">
            <tr v-for="(e, j) in b" :key="j">
                <td>{{e.snapshot_id}}</td>
                <td>{{backupType(e.backup_type)}}</td>
                <td>{{e.target_ip}}</td>
                <td>{{e.status}}</td>
                <td>
                    <button
                            type="button"
                            class="btn btn-secondary mx-1"
                            @click="viewRestoreInfo(e.snapshot_id)"
                    >View
                    </button>
                    <button
                            :disabled="isFailed(e.status)"
                            type="button"
                            class="btn btn-success mx-1"
                            data-toggle="modal"
                            data-target="#restoreCluster"
                            @click="$emit('emitSnapshotId', e.snapshot_id)"
                    >Restore
                    </button>
                </td>
            </tr>
            </tbody>
        </table>
        <div class="row justify-content-end mr-3">
            <button class="btn btn-outline-secondary mx-1" @click="goBack">Back</button>
            <button class="btn btn-primary mx-1" data-toggle="modal" data-target="#registerBackup">Create Backup
            </button>
        </div>
    </div>
</template>

<script>
  export default {
    props: {
      cluster: {
        type: Object,
      },
      backups: {
        type: Object,
      },
      backups_by_snapshot: Array,
    },
    methods: {
      viewRestoreInfo(snapshot_id) {
        this.$router.push(`/clusters/${this.cluster.cluster_id}/data/${snapshot_id}`);
      },
      goBack() {
        this.$router.back();
      },
      backupType(type) {
        if (type === 1) {
          return 'Cluster';
        }
        if (type === 2) {
          return 'Snapshot';
        }
        if (type === 3) {
          return 'Incremental';
        }
      },
      isFailed(status) {
        return status === 'FAILED';
      }
    },
  };
</script>
<style>
    tbody::before {
        content: '';
        display: block;
        height: 30px;
    }
</style>
