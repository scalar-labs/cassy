<template>
    <table class="table table-borderless text-left">
        <thead>
        <tr>
            <th class="text-nowrap" scope="col">Cluster ID</th>
            <th class="text-nowrap" scope="col">Cluster Name</th>
            <th class="text-nowrap" scope="col">Target IPs</th>
            <th class="text-nowrap" scope="col">Keyspaces</th>
        </tr>
        </thead>
        <tbody>
        <tr :class="{'no-data': cluster.cluster_id, 'text-center': true, 'font-italic': true}">
            <td colspan="3">Unable to retrieve cluster data</td>
        </tr>
        <tr>
            <td>{{cluster.cluster_id}}</td>
            <td>{{cluster.cluster_name}}</td>
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
