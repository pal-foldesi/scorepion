local boardKey = KEYS[1]
local member = ARGV[1]
local score = ARGV[2]
local maxCardinality = ARGV[3]

redis.call('ZADD', boardKey, score, member)
local cardinality = redis.call('ZCARD', boardKey)

if cardinality > tonumber(maxCardinality) then
    redis.call('ZPOPMIN', boardKey)
end