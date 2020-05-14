<template>
    <div>
        <h4 class="mt-3">Clusters</h4>
        <table class="table table-borderless text-center text-nowrap table-striped">
            <thead>
            <tr>
                <th scope="col">ID</th>
                <th scope="col">Name</th>
                <th scope="col">Created At</th>
                <th scope="col">Updated At</th>
            </tr>
            </thead>
            <tbody>
            <tr
                    v-for="(c, index) in clusters.entries"
                    :key="index"
            >
                <td>{{c.cluster_id}}</td>
                <td>{{c.cluster_name}}</td>
                <td>{{ parseInt(c.created_at) | moment('YYYY/M/D, h:mm a')}}</td>
                <td>{{ parseInt(c.updated_at) | moment('YYYY/M/D h:mm a')}}</td>
                <td>
                    <button
                            type="button"
                            class="btn btn-secondary"
                            @click="viewBackups(c.cluster_id)"
                    >Manage
                    </button>
                </td>
            </tr>
            </tbody>
        </table>
        <div class="row justify-content-end mr-3">
            <button class="btn btn-primary mx-1" data-toggle="modal" data-target="#registerCluster">Add Cluster</button>
        </div>
    </div>
</template>

<script>
  export default {
    props: {
      clusters: {
        type: Object,
      },
    },
    methods: {
      viewBackups(cluster_id) {
        this.$router.push(`/clusters/${cluster_id}/backups`);
      },
    },
  };
</script>
