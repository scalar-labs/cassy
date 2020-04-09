<template>
    <div class="container">
        <h4 class="mt-3">Clusters</h4>
        <table class="table table-borderless table-striped">
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
                <td>{{ parseInt(c.created_at) | moment("YYYY/M/d, h:mm a")}}</td>
                <td>{{ parseInt(c.updated_at) | moment("YYYY/M/d h:mm a")}}</td>
                <td><button type="button" class="btn btn-secondary">Manage</button></td>
            </tr>
            </tbody>
        </table>
        <div class="row justify-content-end"><button class="btn btn-primary">Add Cluster</button></div>
    </div>
</template>

<script>
  export default {
    props: {
      clusters: {
        type: Object,
      },
    },
  };
</script>
