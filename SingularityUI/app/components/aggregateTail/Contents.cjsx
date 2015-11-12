LogLine = require './LogLine'
Loader = require './Loader'

Utils = require '../../utils'

Contents = React.createClass

  # ============================================================================
  # Lifecycle Methods                                                          |
  # ============================================================================

  getInitialState: ->
    @state =
      isLoading: false
      loadingText: ''
      linesToRender: []

  componentDidMount: ->
    @scrollNode = ReactDOM.findDOMNode(@refs.scrollContainer)
    @currentOffset = parseInt @props.offset

  componentDidUpdate: (prevProps, prevState) ->
    # Scroll to the appropriate place
    # if @state.linesToRender.length > 0 and prevState.linesToRender.length is 0
    #   if !@props.offset
    #     @scrollToBottom()
    #   else
    #     @scrollToLine(0)
    if @tailingPoll
      @scrollToBottom()

    # Start tailing automatically if we can't scroll
    if @props.taskState in Utils.TERMINAL_TASK_STATES
      @stopTailingPoll()
    else if (0 < $('.line').length * 20 <= $(@scrollNode).height()) and !@tailingPoll
      @startTailingPoll()

    # Update our loglines components only if needed
    if prevProps.logLines.length isnt @props.logLines.length
      @setState
        linesToRender: @renderLines()

  # ============================================================================
  # Event Handlers                                                             |
  # ============================================================================

  handleScroll: ->
    node = @scrollNode
    # Are we at the bottom?
    if $(node).scrollTop() + $(node).innerHeight() >= node.scrollHeight - 20
      @startTailingPoll(node)
    # Or the top?
    else if $(node).scrollTop() is 0
      @stopTailingPoll()
      @setState
        isLoading: true
        loadingText: 'Fetching'
      @props.fetchPrevious(=>
        @setState
          isLoading: false
          loadingText: ''
      )
    else
      @stopTailingPoll()

  handleHighlight: (e) ->
    @currentOffset = parseInt $(e.target).attr 'data-offset'
    @setState
      linesToRender: @renderLines()

  startTailingPoll: ->
    # Make sure there isn't one already running
    @stopTailingPoll()
    @setState
      isLoading: true
      loadingText: 'Tailing'
    @tailingPoll = setInterval =>
      @props.fetchNext()
    , 2000

  stopTailingPoll: ->
    if @tailingPoll
      clearInterval @tailingPoll
      @tailingPoll = null
      @setState
        isLoading: false
        loadingText: ''

  # ============================================================================
  # Rendering                                                                  |
  # ============================================================================

  renderError: ->
    if @props.ajaxError.present
      <div className="lines-wrapper">
          <div className="empty-table-message">
              <p>{@props.ajaxError.message}</p>
          </div>
      </div>

  renderLines: ->
    if @props.logLines
      @props.logLines.map((l, i) =>
        <LogLine
          content={l.data}
          offset={l.offset}
          key={i}
          index={i}
          highlighted={l.offset is @currentOffset}
          highlight={@handleHighlight}
          totalLines={@props.logLines.length} />
      )

  lineRenderer: (index, key) ->
    @state.linesToRender[index]

  getLineHeight: (index) ->
    if index in [0, @state.linesToRender.length]
      return 40
    else
      return 20

  render: ->
    <div className="contents-container">
      <div className="tail-contents" ref="scrollContainer" onScroll={_.throttle @handleScroll, 200}>
        {@renderError()}
        <ReactList
          className="infinite"
          ref="lines"
          itemRenderer={@lineRenderer}
          itemSizeGetter={@getLineHeight}
          length={@state.linesToRender.length}
          type="variable"
          useTranslate3d={true}>
        </ReactList>
      </div>
      <Loader isVisable={@state.isLoading} text={@state.loadingText} />
    </div>

  # ============================================================================
  # Utility Methods                                                            |
  # ============================================================================

  scrollToLine: (line) ->
    console.log 'scrollto ' + line
    @refs.lines.scrollTo(line)

  scrollToTop: ->
    console.log 'top'
    @stopTailingPoll()
    @refs.lines.scrollTo(0)

  scrollToBottom: ->
    console.log 'bot'#, arguments.callee.caller
    @refs.lines.scrollTo(@state.linesToRender.length)

module.exports = Contents