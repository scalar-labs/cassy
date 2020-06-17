<template>
    <table class="table table-bordered text-center">
        <thead class="table-success">
        <tr>
            <th scope="col">Cluster ID</th>
            <th scope="col">Target IPs</th>
            <th scope="col">Keyspaces</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td>{{cluster.cluster_id}}</td>
            <td>{{cluster.target_ips}}</td>
            <td>{{cluster.keyspaces}}</td>
        </tr>
        </tbody>
    </table>
</template>

<script>
  export default {
    props: {
      cluster: {},
    },
  };
</script>
